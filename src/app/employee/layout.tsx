import EmployeeSidebar from '@/components/layout/EmployeeSidebar';

export default function EmployeeLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <EmployeeSidebar />
      <main className="flex-1 ml-64">
        {children}
      </main>
    </div>
  );
}
