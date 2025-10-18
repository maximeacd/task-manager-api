import { Link } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../contexts/AuthContext";
import styles from "./Navbar.module.css";

export default function Navbar(){
    const { token, logout } = useContext(AuthContext);

    return(
        <nav className={styles.navbar}>
            <h1 className={styles.logo}>Task Manager</h1>
            <div className={styles.links}>
                {token ? (
                    <>
                        <Link to="/">Tasks</Link>
                        <button onClick={logout} className={styles.logout}>Logout</button>
                    </>
                ):(
                    <>
                        <Link to="/login">Login</Link>
                        <Link to="/register">Register</Link>
                    </>
                )}
            </div>
        </nav>
    );
}