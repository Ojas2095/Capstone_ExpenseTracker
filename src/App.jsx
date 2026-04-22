import { useState, useEffect } from 'react';
import Dashboard from './components/Dashboard';
import TransactionForm from './components/TransactionForm';
import TransactionList from './components/TransactionList';
import Login from './components/Login';

function App() {
  const [transactions, setTransactions] = useState([]);
  const [user, setUser] = useState(null);

  // Fetch transactions from Java Backend
  useEffect(() => {
    if (user) {
      fetch('http://localhost:8080/api/transactions', {
        headers: { 'Authorization': `Bearer ${user.token}` }
      })
        .then(res => res.json())
        .then(data => setTransactions(data))
        .catch(err => console.error("Error fetching data:", err));
    }
  }, [user]);

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    setUser(null);
    setTransactions([]);
  };

  const addTransaction = (transaction) => {
    fetch('http://localhost:8080/api/transactions', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.token}`
      },
      body: JSON.stringify(transaction)
    })
    .then(res => res.json())
    .then(() => {
      // Refresh transactions
      return fetch('http://localhost:8080/api/transactions', {
        headers: { 'Authorization': `Bearer ${user.token}` }
      });
    })
    .then(res => res.json())
    .then(data => setTransactions(data))
    .catch(err => console.error("Error adding:", err));
  };

  const deleteTransaction = (id) => {
    fetch(`http://localhost:8080/api/transactions?id=${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${user.token}` }
    })
    .then(() => {
      setTransactions(transactions.filter(t => t.id !== id));
    })
    .catch(err => console.error("Error deleting:", err));
  };

  // Derive stats
  const totalBalance = transactions.reduce((acc, curr) => 
    curr.type === 'income' ? acc + curr.amount : acc - curr.amount, 0
  );
  const income = transactions
    .filter(t => t.type === 'income')
    .reduce((acc, curr) => acc + curr.amount, 0);
  const expense = transactions
    .filter(t => t.type === 'expense')
    .reduce((acc, curr) => acc + curr.amount, 0);

  const exportCSV = () => {
    // Generate CSV String
    const header = ["ID,Text,Amount,Type,Category,Date\n"];
    const rows = transactions.map(t => 
      `"${t.id}","${t.text}","${t.amount}","${t.type}","${t.category}","${t.date}"\n`
    );
    const csvContent = "data:text/csv;charset=utf-8," + header.concat(rows).join("");
    
    // Trigger download
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "ExpenseReport_Capstone.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (!user) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <div className="app-container">
      {/* Sidebar / Form Area */}
      <aside className="glass-panel" style={{ padding: '2rem' }}>
        <div className="header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h1>TrackPulse</h1>
            <p>Your Financial Pulse</p>
          </div>
          <button 
            onClick={handleLogout} 
            className="glass-button" 
            style={{ padding: '8px 16px', background: 'transparent', border: '1px solid var(--border-light)', color: 'var(--text-muted)', boxShadow: 'none' }}
          >
            Logout
          </button>
        </div>
        <TransactionForm onAdd={addTransaction} />
        
        <div style={{ marginTop: '2.5rem' }}>
          <h3 style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '0.5rem', textTransform: 'uppercase' }}>Advanced Features</h3>
          <button 
            onClick={exportCSV}
            className="glass-button" 
            style={{ width: '100%', background: 'linear-gradient(135deg, #10b981, #059669)', color: 'white' }}>
             Download CSV Report
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main>
        <Dashboard 
          balance={totalBalance} 
          income={income} 
          expense={expense} 
          transactions={transactions} 
        />
        <TransactionList 
          transactions={transactions} 
          onDelete={deleteTransaction} 
        />
      </main>
    </div>
  );
}

export default App;
