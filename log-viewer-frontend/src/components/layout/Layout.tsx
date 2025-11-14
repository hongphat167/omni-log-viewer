import { ReactNode } from 'react';
import { Moon, Sun } from 'lucide-react';

interface LayoutProps {
  children: ReactNode;
  darkMode: boolean;
  onToggleDarkMode: () => void;
}

export const Layout = ({ children, darkMode, onToggleDarkMode }: LayoutProps) => {
  return (
    <div className="min-h-screen bg-slate-900 text-slate-100">
      <header className="bg-slate-800 border-b border-slate-700 px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-blue-400">Omni Log Viewer</h1>
            <p className="text-sm text-slate-400">Real-time microservices log monitoring</p>
          </div>
          <button
            onClick={onToggleDarkMode}
            className="p-2 rounded-lg bg-slate-700 hover:bg-slate-600 transition-colors"
          >
            {darkMode ? <Sun size={20} /> : <Moon size={20} />}
          </button>
        </div>
      </header>
      <main className="h-[calc(100vh-73px)]">
        {children}
      </main>
    </div>
  );
};
