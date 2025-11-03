import { IoNotificationsOutline } from "react-icons/io5";

export default function Header({ title }: { title: string }) {
  return (
    <div className="flex justify-between items-center bg-white p-2 md:p-3 shadow-sm border-b fixed top-0 left-0 w-full h-16 md:pl-56 z-30 md:ml-10">
      <h1 className="text-lg sm:text-xl md:text-2xl lg:text-3xl font-bold ml-24">{title}</h1>
      <IoNotificationsOutline className="text-gray-700 mr-32" size={28} />
    </div>
  );
}
