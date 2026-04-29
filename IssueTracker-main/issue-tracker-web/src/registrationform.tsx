import axios from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import config from "./config";

function Registrationform() {
  const [firstName, setfirstName] = useState("");
  const [lastName, setlastName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handlesubmit = async (e: any) => {
    e.preventDefault();
    console.log(firstName, password, email);
    await axios
      .post(`${config.apiUrl}/api/auth/register`, {
        firstName: firstName,
        lastName: lastName,
        password: password,
        email: email,
        role: "ADMIN",
      })
      .then(() => {
        toast.success("Registered successfully");
        navigate("/login", { replace: true });
      })
      .catch(() => {
        toast.error("Not registered successfully");
      });
    setfirstName("");
    setlastName("");
    setPassword("");
    setEmail("");
  };

  return (
    <div>
      <div className="hero min-h-screen bg-base-200">
        <div className="card w-full max-w-xl shrink-0 bg-base-100 shadow-2xl">
          <label className="  pt-4 text-center text-3xl font-bold">
            Register
          </label>
          <form onSubmit={handlesubmit} className="card-body">
            <div className="jutsify-between  flex">
              <div className="form-control mr-3 flex-1">
                <label className="label">
                  <span className="label-text">Firstname</span>
                </label>
                <input
                  type="text"
                  placeholder="firstname"
                  className="input input-bordered"
                  onChange={(e) => setfirstName(e.target.value)}
                  required
                />
              </div>
              <div className="form-control ml-3 flex-1">
                <label className="label">
                  <span className="label-text">Lastname</span>
                </label>
                <input
                  type="text"
                  placeholder="lastname"
                  className="input input-bordered"
                  onChange={(e) => setlastName(e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="form-control">
              <label className="label">
                <span className="label-text">Email</span>
              </label>
              <input
                type="email"
                placeholder="email"
                className="input input-bordered"
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="form-control">
              <label className="label">
                <span className="label-text">Password</span>
              </label>
              <input
                type="password"
                placeholder="password"
                className="input input-bordered"
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <label className="label">
                <div className="link-hover link label-text-alt">
                  <Link to="/login">Already Registered? Sign in</Link>
                </div>
              </label>
            </div>
            <div className="form-control mt-6">
              <button className="btn btn-primary">Register</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    // </div>
  );
}
export default Registrationform;
