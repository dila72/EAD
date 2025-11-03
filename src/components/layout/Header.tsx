import { IoNotificationsOutline } from "react-icons/io5";

export default function Header({ title }: { title: string }) {
  return (
    <div className="flex justify-between items-center bg-white p-4 shadow-sm border-b fixed top-0 left-0 w-full md:pl-64 z-30 ml-10  ">
      <h1 className="text-3xl font-bold">{title}</h1>
      <IoNotificationsOutline className="text-gray-700 mr-16" size={32} />
    </div>
  );
}
