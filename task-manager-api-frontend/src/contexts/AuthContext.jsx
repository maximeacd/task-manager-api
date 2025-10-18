import { createContext, useState, useEffect } from "react";
import axios from "../api/axiosConfig";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [user, setUser] = useState(null);

    useEffect(() => {
        if (token) {
            axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        }
        else{
            delete axios.defaults.headers.common["Authorization"];
        }
    },[token]);

    const login = async (username, password) => {
        const res = await axios.post("/auth/login", {username, password});
        const jwt = res.data;
        localStorage.setItem("token", jwt);
        setToken(jwt);
    };

    const register = async (username, password) => {
        await axios.post("/auth/register", {username, password});
    };

    const logout = () => {
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{token, user, login, register, logout}}>
            {children}
        </AuthContext.Provider>
    );
};