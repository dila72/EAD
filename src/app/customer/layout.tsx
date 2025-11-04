'use client';

import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { usePathname } from "next/navigation";

export default function CustomerLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  
  // Extract page title from pathname
  const getPageTitle = () => {
    if (pathname?.includes('/my-appointments')) return 'My Appointments';
    if (pathname?.includes('/my-projects')) return 'My Projects';
    if (pathname?.includes('/dashboard')) return 'Dashboard';
    if (pathname?.includes('/vehicles')) return 'My Vehicles';
    if (pathname?.includes('/profile')) return 'Profile';
    if (pathname?.includes('/chatbot')) return 'Chatbot';
    return 'Dashboard';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <Header title={getPageTitle()} />
      
      {/* Main Content Area with responsive margins */}
      <div className="md:ml-16 lg:ml-16 xl:ml-80 md:mr-16 lg:mr-16 xl:mt-16">
        <div className="pt-16 px-6 md:px-8 lg:px-10 mt-4 mb-10">
          {children}
        </div>
      </div>
    </div>
  );
}
