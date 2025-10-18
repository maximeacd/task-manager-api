import { useState } from "react";
import axios from "../api/axiosConfig";
import styles from "./TaskCard.module.css";

export default function TaskCard({ task, onDelete, fetchTasks, onChangeStatus, onUpdateTaskLocal }) {
  const [showModal, setShowModal] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const [editField, setEditField] = useState(null);
  const [editedValue, setEditedValue] = useState("");

const handleStart = () => onChangeStatus(task.id, "IN_PROGRESS");

const handleDone = () => onChangeStatus(task.id, "DONE");

const handleEditClick = (field, currentValue) => {
    setEditField(field);
    setEditedValue(currentValue || "");
};

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
        console.error("Erreur lors de la mise à jour :", err);
    } finally {
        setIsUpdating(false);
    }
};

const statusClass =
    task.status === "TO_BE_DONE"
        ? styles.toBeDone 
        : task.status === "IN_PROGRESS"
        ? styles.inProgress
        :styles.done;

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
            {task.status === "TO_BE_DONE" && (
                <button
                    onClick={handleStart}
                    disabled={isUpdating}
                    className={styles.startBtn}
                >
                    {isUpdating ? "..." : "▶️ Start"}
                </button>
            )}
            {task.status === "IN_PROGRESS" && (
                <button
                onClick={handleDone}
                className={styles.startBtn}
                >
                    ✅ Done
                </button>
            )}
            <button onClick={() => setShowModal(true)}>➕</button>
            <button onClick={onDelete}>🗑️</button>
        </div>
      </div>

      {showModal && (
        <div className={styles.modalOverlay} onClick={() => setShowModal(false)}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h2>
                {editField === "title" ? (
                    <>
                        <input
                            type="text"
                            value={editedValue}
                            onChange={e => setEditedValue(e.target.value)}
                    />
                    <button onClick={handleSave} disabled={isUpdating} className={`${styles.iconBtn} ${styles.saveBtn}`}>💾</button>
                    </>
                ) : (
                    <>
                        {task.title}{" "}
                        <button onClick={() => handleEditClick("title", task.title)} className={`${styles.iconBtn} ${styles.editBtn}`}>✏️</button>
                    </>
                )}
            </h2>

            <p>
                <strong>Description:</strong> {" "}
                {editField === "description" ? (
                <>
                    <textarea
                        value={editedValue}
                        onChange={e => setEditedValue(e.target.value)}
                        rows="3"
                    />
                    <button onClick={handleSave} disabled={isUpdating} className={`${styles.iconBtn} ${styles.saveBtn}`}>💾</button>
                    </>
                ) : (
                    <>
                        {task.description}{" "}
                        <button onClick={() => handleEditClick("description", task.description)} className={`${styles.iconBtn} ${styles.editBtn}`}>✏️</button>
                    </>
                )}
            </p>

            <p>
                <strong>Due date:</strong> {" "}
                {editField === "dueDate" ? (
                    <>
                        <input
                            type="date"
                            value={editedValue || ""}
                            onChange={e => setEditedValue(e.target.value)}
                        />
                        <button onClick={handleSave} disabled={isUpdating} className={`${styles.iconBtn} ${styles.saveBtn}`}>💾</button>
                        </>
                ) : (
                    <>
                        {task.dueDate || "-"}{" "}
                        <button onClick={() => handleEditClick("dueDate", task.dueDate)} className={`${styles.iconBtn} ${styles.editBtn}`}>✏️</button>
                        </>
                )}
            </p>

            <p><strong>Status:</strong> {task.status}</p>
            
            <button onClick={() => setShowModal(false)}>Fermer</button>
          </div>
        </div>
      )}
    </>
  );
}