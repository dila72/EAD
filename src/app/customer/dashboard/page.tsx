import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";

export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <Header title="Dashboard" />

      <div className="pt-20 md:pl-64 px-4">
        <p className="text-gray-700">Hello,</p>
        <h2 className="text-xl font-semibold mb-4">Hi James</h2>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 mb-6">
          <div className="bg-white rounded-lg shadow-md p-3 text-center">
            <p className="text-2xl font-bold text-red-500">3</p>
            <p>Vehicles</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-3 text-center">
            <p className="text-2xl font-bold text-yellow-500">2</p>
            <p>Upcoming Appointments</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-3 text-center">
            <p className="text-2xl font-bold text-green-500">1</p>
            <p>Projects</p>
          </div>
        </div>

        {/* Upcoming Appointments */}
        <h3 className="font-semibold mb-2">Upcoming Appointments</h3>
        <div className="bg-blue-500 text-white rounded-lg p-3 mb-4">
          <p className="font-semibold">Brake Pad Checking</p>
          <p className="text-sm">Toyota ABC-2345</p>
          <div className="flex justify-between text-sm mt-2">
            <span>Sunday, 12 June</span>
            <span>11:00AM–12:00AM</span>
          </div>
        </div>

        {/* Ongoing Projects */}
        <h3 className="font-semibold mb-2">Ongoing Projects</h3>
        <div className="bg-blue-500 text-white rounded-lg p-3">
          <p className="font-semibold">Seats Repairing</p>
          <p className="text-sm">Toyota CBB-5475</p>
          <p className="text-sm mt-1">Starting Date: Sunday, 12 June</p>
        </div>
      </div>
    </div>
  );
}
