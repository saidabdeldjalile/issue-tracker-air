/// <reference types="vite/client" />

declare module 'frappe-gantt' {
  const Gantt: any;
  export default Gantt;
}

declare module 'frappe-gantt/dist/frappe-gantt.css' {
  const content: string;
  export default content;
}