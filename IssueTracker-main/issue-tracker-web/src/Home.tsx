import { useNavigate } from "react-router-dom"

function Home() {
    const navigate = useNavigate();

  return (
    <>
    <div className="hero min-h-screen bg-base-200">
  <div className="hero-content text-center">
    <h1 className="text-5xl font-bold max-w-md">Welcome to IssueTracker</h1>
    <div className="max-w-md">
      
      <p className="py-6">Create organisations/projects, add your team members, log and manage your issues. Automatically detect duplicates and much more.</p>
      <button className="btn btn-primary mx-2">Create Your Organisation</button>
      <button className="btn btn-secondary mx-2"
      onClick={()=>{
        navigate("/projects");
      }}
      >View Projects</button>
    </div>
  </div>
</div>
    </>
  )
}
export default Home