import { BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import { AuthProvider, AuthContext } from "./contexts/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TasksPage from "./pages/TasksPage";
import Navbar from "./components/Navbar";
import { useContext } from "react";

function PrivateRoute({ children }){
  const { token } = useContext(AuthContext);
  return token ? children : <Navigate to="/login" />;
}

export default function App(){
  return (
    <AuthProvider>
      <Router>
        <AuthWrapper>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/tasks" element={<PrivateRoute><TasksPage /></PrivateRoute>} /> 
            <Route path="/" element={<HomeRedirect/>}/>
          </Routes>
        </AuthWrapper>
      </Router>
    </AuthProvider>
  );
}

function AuthWrapper({children}){
  const { token } = useContext(AuthContext);
  return (
    <>
    {token && <Navbar/>}
    {children}
    </>
  );
} 

function HomeRedirect(){
  const { token } = useContext(AuthContext);
  return <Navigate to={token ? "/tasks" : "/login"}replace/>
}