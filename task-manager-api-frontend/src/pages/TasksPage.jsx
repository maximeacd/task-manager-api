import { useState, useEffect, use } from "react";
import axios from "../api/axiosConfig";
import TaskCard from "../components/TaskCard";
import styles from "./TasksPage.module.css";

export default function TasksPage(){
    const [tasks, setTasks] = useState([]);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState("")
    const [showModal, setShowModal] = useState(false);
    const [searchTitle, setSearchTitle] = useState(""); 
    const [isFocused, setIsFocused] = useState(false);
    const [sortOption, setSortOption] = useState("none");

    const fetchTasks = async () => {
        const res = await axios.get("/tasks/all");
        setTasks(res.data.content);
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    const addTask = async e => {
        e.preventDefault();
        await axios.post("/tasks", {title, description,dueDate});
        setTitle("");
        setDescription("");
        setDueDate("");
        fetchTasks();
    };

    const deleteTask = async id => {
        await axios.delete(`/tasks/${id}`);
        fetchTasks();
    };

    const changeTaskStatus = async (id, newStatus) => {
        setTasks(prev =>
            prev.map(task =>
                task.id === id ? {...task, status: newStatus} : task
            )
        );

        try{
            await axios.patch(`/tasks/${id}/status?status=${newStatus}`);
        } catch (err){
            console.error("Erreur de mise à jour du statut :", err);
            fetchTasks();
        }
    };

    const updateTaskLocal = (id, updatedFields) => {
        setTasks(prev =>
            prev.map(task =>
                task.id === id ? {...task, ...updatedFields} : task
            )
        );
    };

    const filteredTasks = tasks.filter((t) =>
        t.title.toLowerCase().includes(searchTitle.toLowerCase())
    );

    const sortTasks = (taskList) => {
            if(sortOption === "asc"){
                return [...taskList].sort(
                    (a,b) => new Date(a.dueDate) - new Date(b.dueDate)
                );
            } else if (sortOption === "desc"){
                return [... taskList].sort(
                    (a,b) => new Date(b.dueDate) - new Date(a.dueDate)
                );
            }
            return taskList;
    };

    const groupedTasks = {
        inProgress: sortTasks(filteredTasks.filter(t => t.status === "IN_PROGRESS")),
        toBeDone: sortTasks(filteredTasks.filter(t => t.status === "TO_BE_DONE")),
        done: sortTasks(filteredTasks.filter(t => t.status === "DONE"))
    };

    const updateTaskStatus = (id, newStatus) => {
        setTasks(prevTasks =>
            prevTasks.map(task =>
                task.id === id ? { ...task, status: newStatus} : task
            )
        );
        console.log("Changement de statut local :", id, "→", newStatus);
    };

    return (
        <div style={{maxWidth: "600px", margin: "20px auto", textAlign: "-webkit-center", color: "black"}}>
            <h2 style={{ color: "white" }}>My tasks</h2>

            <div>                
            <input
                type="text"
                placeholder={searchTitle === "" ? (isFocused ? "" : "🔍 Search by title...") : ""}
                value={searchTitle}
                onChange={(e) => setSearchTitle(e.target.value)}
                onFocus={() => setIsFocused(true)}
                onBlur={() => setIsFocused(false)}
                style={{
                    width: "70%",
                    padding: "8px",
                    marginTop: "15px",
                    borderRadius: "6px",
                    border: "1px solid #ccc",
                    fontSize: "14px",
                }}
            />
            {searchTitle && (
                <button
                    onClick={() => setSearchTitle("")}
                    style={{
                    marginLeft: "5px",
                    background: "transparent",
                    border: "none",
                    cursor: "pointer",
                    color: "white",
                    fontSize: "16px",
                    }}
                >
                    ✖️
                </button>
                )}        

            <button onClick={() => setShowModal(true)}>New Task</button>
            
            </div>

            <div style={{marginTop:"15px"}}>
                <label style={{color: "white", marginRight: "8px"}}>Sort by due date:</label>
                <select
                    value={sortOption}
                    onChange={(e) => setSortOption(e.target.value)}
                    style={{
                        padding: "5px 10px",
                        borderRadius: "5px",
                        border: "1px solid #ccc",
                    }}
                >
                    <option value="none">None</option>
                    <option value="asc">Ascending</option>
                    <option value="desc">Descending</option>
                </select>
            </div>

            {showModal && (
                <div className={styles.modalOverlayStyle}
                onClick={() => setShowModal(false)}
                >
                <div className={styles.modalContentStyle}
                onClick={e => e.stopPropagation()}
                >
                    <form onSubmit={async(e) => {
                        e.preventDefault();
                        await addTask(e);
                        setShowModal(false);
                    }}>
                    <input
                        type="text"
                        placeholder="Title"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Description"
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        required
                    />
                    <input
                        type="date"
                        placeholder="Due date"
                        value={dueDate}
                        onChange={e => setDueDate(e.target.value)}
                    />
                    <div style={{ marginTop: "10px" }}>
                        <button type="submit">Create</button>
                        <button type="button" onClick={() => setShowModal(false)} style={{ marginLeft: "10px" }}>
                        Cancel
                        </button>
                    </div>
                    </form>
                </div>
                </div>
            )}

            {[
                {label: "IN PROGRESS", list: groupedTasks.inProgress},
                {label: "TO BE DONE", list: groupedTasks.toBeDone},
                {label: "DONE", list: groupedTasks.done}
            ].map(section => (
                <div key={section.label} style={{marginTop:"20px"}}>
                    <h3 style={{
                                    textTransform: "uppercase",
                                    fontWeight: "bold",
                                    borderBottom: "1px solid #ccc",
                                    paddingBottom: "5px",
                                    color:
                                    section.label === "IN PROGRESS"
                                        ? "#ffb703" // jaune/orange
                                        : section.label === "TO BE DONE"
                                        ? "#219ebc" // bleu
                                        : "#38b000" // vert
                                }}>
                        {section.label} ({section.list.length})
                    </h3>

                    {section.list.length === 0 ? (
                        <p style={{color: "#999"}}> No tasks</p>
                    ):(
                        section.list.map(task => (
                            <TaskCard
                            key={task.id}
                            task={task}
                            onDelete={() => deleteTask(task.id)}
                            fetchTasks={fetchTasks}
                            onChangeStatus={changeTaskStatus}
                            onUpdateTaskLocal={updateTaskLocal}
                            />
                        ))
                    )}
                </div>   
            ))}
        </div>
    );
}