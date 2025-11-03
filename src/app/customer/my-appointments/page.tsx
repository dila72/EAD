import Sidebar from "@/components/layout/Sidebar";
import TopBar from "@/components/layout/Header";

export default function MyAppointments() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <TopBar title="My Appointments" />

      <div className="pt-20 md:pl-64 px-4">
        <p className="text-gray-700">Hello,</p>
        <h2 className="text-xl font-semibold mb-4">Hi James</h2>

        {/* Stats */}
        <div className="grid grid-cols-2 gap-3 mb-6">
          <div className="bg-white rounded-lg shadow-md p-3 text-center">
            <p className="text-2xl font-bold text-green-500">3</p>
            <p>Completed</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-3 text-center">
            <p className="text-2xl font-bold text-yellow-500">2</p>
            <p>Upcoming</p>
          </div>
        </div>

        {/* Upcoming Appointments */}
        <div className="flex justify-between items-center mb-2">
          <h3 className="font-semibold">Upcoming Appointments</h3>
          <button className="bg-orange-400 text-white px-3 py-1 rounded text-sm">+ New</button>
        </div>
        <div className="overflow-x-auto bg-white rounded-lg shadow-md text-sm mb-6">
          <table className="w-full text-left">
            <thead className="bg-gray-100 text-gray-700">
              <tr>
                <th className="p-2">ID</th>
                <th className="p-2">Service</th>
                <th className="p-2">Vehicle</th>
                <th className="p-2">Date</th>
                <th className="p-2">Time</th>
                <th className="p-2">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr className="border-t">
                <td className="p-2">#1234</td>
                <td className="p-2">Checkup</td>
                <td className="p-2">ABD2040</td>
                <td className="p-2">10.09.2020</td>
                <td className="p-2">11.00am–12.00am</td>
                <td className="p-2 text-orange-500 font-semibold">Upcoming</td>
              </tr>
              <tr className="border-t">
                <td className="p-2">#1235</td>
                <td className="p-2">Oil Change</td>
                <td className="p-2">CBA3050</td>
                <td className="p-2">17.09.2025</td>
                <td className="p-2">11.00am–12.00am</td>
                <td className="p-2 text-orange-500 font-semibold">Upcoming</td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* Completed */}
        <h3 className="font-semibold mb-2">Completed Appointments</h3>
        <div className="overflow-x-auto bg-white rounded-lg shadow-md text-sm">
          <table className="w-full text-left">
            <thead className="bg-gray-100 text-gray-700">
              <tr>
                <th className="p-2">ID</th>
                <th className="p-2">Service</th>
                <th className="p-2">Vehicle</th>
                <th className="p-2">Date</th>
                <th className="p-2">Time</th>
                <th className="p-2">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr className="border-t">
                <td className="p-2">#1234</td>
                <td className="p-2">Checkup</td>
                <td className="p-2">ABD2040</td>
                <td className="p-2">10.09.2020</td>
                <td className="p-2">11.00am–12.00am</td>
                <td className="p-2 text-green-600 font-semibold">Completed</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
