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
        <Navbar />
        <Routes>
          <Route path="/" element={<PrivateRoute><TasksPage /></PrivateRoute>} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}