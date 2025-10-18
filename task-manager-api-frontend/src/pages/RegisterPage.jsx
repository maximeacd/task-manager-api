import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";
import { Link } from "react-router-dom";
import styles from "./Auth.module.css";

export default function RegisterPage(){
    const { register, login } = useContext(AuthContext);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async e => {
        e.preventDefault();
        try{
            await register(username, password);
            await login (username, password);
            navigate("/");
        }
        catch{
            alert("Failed");
        }
    };

    return(
        <div>
            <div className={styles.container}>
                <h2>Register</h2>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <input type="username" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
                    <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                    <button type="submit">Create an account</button>
                </form>
            </div>

            <div className={styles.haveAnaccountOrNot}>
                <p>
                    Already have an Account?{" "}
                    <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}