import axios from "./api/axios";
import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "./hooks/useAuth.js";
import { toast } from "react-toastify";

function Loginform() {
  const { setAuth } = useAuth();

  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/projects";

  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");

  const handlesubmit = async (e: any) => {
    e.preventDefault();
    console.log(password, email);
    await axios
      .post(
        `/auth/auth`,
        JSON.stringify({
          password,
          email,
        }),
        {
          // withCredentials: true,
        },
      )
      .then((res) => {
        const token = res.data.token;
        const role = res.data.role;
        console.log(res);
        setAuth({ email, password, token, role });
        setPassword("");
        setEmail("");
        navigate(from, { replace: true });
        toast.success("Logged in successfully");
      })
      .catch(() => {
        //create a toast saying no details foud or password is incorrect
        toast.error("Invalid email or password");
      });
  };

  return (
    <div>
      <div className="hero min-h-screen bg-base-200">
        {/* <div className="hero-content flex-col lg:flex-row-reverse"> */}
        {/* <div className="text-center lg:text-left">
            <h1 className="text-5xl font-bold">Login now!</h1>
            <p className="py-6">
              Provident cupiditate voluptatem et in. Quaerat fugiat ut assumenda
              excepturi exercitationem quasi. In deleniti eaque aut repudiandae et
              a id nisi.
            </p>
          </div> */}
        <div className="card w-full max-w-xl shrink-0 bg-base-100 shadow-2xl">
          <label className=" pt-4 text-center text-3xl font-bold">Login</label>
          <form onSubmit={handlesubmit} className="card-body">
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
                  <Link to="/register">Not Registered? Sign up</Link>
                </div>
              </label>
            </div>
            <div className="form-control mt-6">
              <button className="btn btn-primary">Login</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    // </div>
  );
}
export default Loginform;
