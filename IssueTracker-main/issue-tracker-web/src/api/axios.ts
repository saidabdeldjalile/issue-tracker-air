import axios from "axios";
import config from "../config";

export default axios.create({
  baseURL: `${config.apiUrl}/api`,
  headers: {
    "Content-Type": "application/json",
  },
});