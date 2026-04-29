export interface TicketResponse {
  // Define the properties of the Ticket type here
  id: number;
  title: string;
  description: string;
  status: string;
  priority: string;
  comments: Comment[];
  createdAt: string;
  modifiedAt: string;
  created: User;
  assigned: User;
  project: Project;
}
export interface User {
  firstName: string;
  lastname: string;
  email: string;
}
export interface Comment {
  email: string;
  comment: string;
  created: string;
  username: string;
}

export interface Project {
  id: number;
  name: string;
}
