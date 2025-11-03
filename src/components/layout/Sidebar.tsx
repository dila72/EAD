"use client";
import { useState } from "react";
import { FiMenu, FiLogOut } from "react-icons/fi";
import { FaCar, FaCalendarAlt, FaProjectDiagram, FaUser, FaComments } from "react-icons/fa";
import Link from "next/link";

export default function Sidebar() {
  const [open, setOpen] = useState(false);

  return (
    <>
      {/* Toggle Button (Mobile) */}
      <button
        onClick={() => setOpen(!open)}
        className="p-2 text-gray-700 md:hidden fixed top-3 left-3 bg-gray-100 rounded-full shadow"
      >
        <FiMenu size={20} />
      </button>

      {/* Sidebar */}
      <div
        className={`fixed top-0 left-0 h-full bg-white w-64 p-4 flex flex-col transition-transform duration-300 z-40 shadow-md
        ${open ? "translate-x-0" : "-translate-x-full"} md:translate-x-0`}
      >
        <div className="flex flex-col items-center mb-6">
          <div className="bg-blue-500 rounded-full w-24 h-24 flex items-center justify-center text-white text-sm font-medium">
            User Photo
          </div>
          <p className="mt-2 font-semibold text-gray-800">User name</p>
        </div>

        <nav className="flex flex-col gap-2 text-gray-700">
          <Link href="/dashboard" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaCar /> Dashboard
          </Link>
          <Link href="/vehicles" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaCar /> My Vehicles
          </Link>
          <Link href="/appointments" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaCalendarAlt /> My Appointments
          </Link>
          <Link href="/projects" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaProjectDiagram /> My Projects
          </Link>
          <Link href="/profile" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaUser /> Profile
          </Link>
          <Link href="/chatbot" className="flex items-center gap-3 p-2 rounded-md hover:bg-blue-500">
            <FaComments /> Chatbot
          </Link>
        </nav>

        <button className="mt-auto flex items-center gap-2 text-red-600 font-semibold hover:underline">
          <FiLogOut /> Logout
        </button>
      </div>
    </>
  );
}
