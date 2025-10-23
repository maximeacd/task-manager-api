import { useState } from "react";
import axios from "../api/axiosConfig";
import styles from "./TaskCard.module.css";

export default function TaskCard({ task, onDelete, fetchTasks, onChangeStatus, onUpdateTaskLocal }) {
  const [showModal, setShowModal] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const [editField, setEditField] = useState(null);
  const [editedValue, setEditedValue] = useState("");

const handleEditClick = (field, currentValue) => {
    setEditField(field);
    setEditedValue(currentValue || "");
};

const isEditable = task.status !== "DONE";

const handleSave = async () => {
    try {
        setIsUpdating(true);
        onUpdateTaskLocal(task.id, {[editField]:editedValue});
        const fieldToUpdate = editField;
        setEditField(null);
        await axios.put(`/tasks/${task.id}`, {
            ...task,
            [fieldToUpdate]: editedValue,
        });
        setShowModal(false);
        if(fetchTasks) await fetchTasks();
    } catch (err) {
        console.error("Update error :", err);
    } finally {
        setIsUpdating(false);
    }
};

const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    setIsUpdating(true);
    try{
        await onChangeStatus(task.id, newStatus);
    }catch (err) {
        console.error("Status update error :", err);
    } finally {
        setIsUpdating(false);
    }
}

const statusClass =
    task.status === "TO_BE_DONE"
        ? styles.toBeDone 
        : task.status === "IN_PROGRESS"
        ? styles.inProgress
        :styles.done;

const getAvailableStatusOptions = () => {
    switch (task.status) {
        case "TO_BE_DONE":
            return ["TO_BE_DONE", "IN_PROGRESS"];
        case "IN_PROGRESS":
            return ["TO_BE_DONE", "IN_PROGRESS", "DONE"];
        default:
            return [];
    }
};

return (
<>
    <div className={`${styles.card} ${statusClass}`}>
    <span>
        {task.title}
        {task.dueDate && (
        <span style={{ fontWeight: "normal", color: "#555" }}>
            {" "}({task.dueDate})
        </span>
        )}
    </span>

    <div>
        {task.status !== "DONE" && (
        <select
            value={task.status}
            onChange={handleStatusChange}
            disabled={isUpdating}
            className={styles.statusSelect}
        >
            {getAvailableStatusOptions().map((status) => (
            <option key={status} value={status}>
                {status.replace(/_/g, " ")}
            </option>
            ))}
        </select>
        )}

        <button onClick={() => setShowModal(true)}>➕</button>
        <button onClick={onDelete}>🗑️</button>
    </div>
    </div>

    {showModal && (
    <div className={styles.modalOverlay} onClick={() => setShowModal(false)}>
        <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h2>
            {editField === "title" ? (
            <>
                <input
                type="text"
                value={editedValue}
                onChange={(e) => setEditedValue(e.target.value)}
                />
                <button
                onClick={handleSave}
                disabled={isUpdating}
                className={`${styles.iconBtn} ${styles.saveBtn}`}
                >
                💾
                </button>
            </>
            ) : (
            <>
                {task.title}{" "}
                {isEditable && (
                <button
                    onClick={() => handleEditClick("title", task.title)}
                    className={`${styles.iconBtn} ${styles.editBtn}`}
                >
                    ✏️
                </button>
                )}
            </>
            )}
        </h2>

        <p>
            <strong>Description:</strong>{" "}
            {editField === "description" ? (
            <>
                <textarea
                value={editedValue}
                onChange={(e) => setEditedValue(e.target.value)}
                rows="3"
                />
                <button
                onClick={handleSave}
                disabled={isUpdating}
                className={`${styles.iconBtn} ${styles.saveBtn}`}
                >
                💾
                </button>
            </>
            ) : (
            <>
                {task.description}{" "}
                {isEditable && (
                <button
                    onClick={() => handleEditClick("description", task.description)}
                    className={`${styles.iconBtn} ${styles.editBtn}`}
                >
                    ✏️
                </button>
                )}
            </>
            )}
        </p>

        <p>
            <strong>Due date:</strong>{" "}
            {editField === "dueDate" ? (
            <>
                <input
                type="date"
                value={editedValue || ""}
                onChange={(e) => setEditedValue(e.target.value)}
                />
                <button
                onClick={handleSave}
                disabled={isUpdating}
                className={`${styles.iconBtn} ${styles.saveBtn}`}
                >
                💾
                </button>
            </>
            ) : (
            <>
                {task.dueDate || "-"}{" "}
                {isEditable && (
                <button
                    onClick={() => handleEditClick("dueDate", task.dueDate)}
                    className={`${styles.iconBtn} ${styles.editBtn}`}
                >
                    ✏️
                </button>
                )}
            </>
            )}
        </p>

        <p>
            <strong>Status:</strong> {task.status}
        </p>

        <button onClick={() => setShowModal(false)}>Close</button>
        </div>
    </div>
    )}
    </>
  );
}