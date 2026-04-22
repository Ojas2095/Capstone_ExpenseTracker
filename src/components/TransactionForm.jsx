import { useState } from 'react';

export default function TransactionForm({ onAdd }) {
  const [text, setText] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('expense');
  const [category, setCategory] = useState('General');

  const categories = [
    'General', 'Food', 'Transport', 'Utilities', 'Entertainment', 'Shopping', 'Salary'
  ];

  const onSubmit = (e) => {
    e.preventDefault();
    if (!text || !amount) return;

    onAdd({
      text,
      amount: parseFloat(amount),
      type,
      category,
      date: new Date().toISOString()
    });

    setText('');
    setAmount('');
  };

  return (
    <div style={{ marginTop: '2rem' }}>
      <h3 style={{ marginBottom: '1.5rem', color: 'var(--primary-accent)' }}>Add New Transaction</h3>
      
      <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Type</label>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
            <button
              type="button"
              onClick={() => setType('expense')}
              style={{
                padding: '10px', borderRadius: '8px', border: '1px solid var(--border-light)', cursor: 'pointer',
                background: type === 'expense' ? 'rgba(239, 68, 68, 0.2)' : 'transparent',
                color: type === 'expense' ? '#fca5a5' : 'var(--text-muted)',
                borderColor: type === 'expense' ? '#ef4444' : 'var(--border-light)',
                transition: '0.3s ease'
              }}
            >
              Expense
            </button>
            <button
              type="button"
              onClick={() => setType('income')}
              style={{
                padding: '10px', borderRadius: '8px', border: '1px solid var(--border-light)', cursor: 'pointer',
                background: type === 'income' ? 'rgba(16, 185, 129, 0.2)' : 'transparent',
                color: type === 'income' ? '#6ee7b7' : 'var(--text-muted)',
                borderColor: type === 'income' ? '#10b981' : 'var(--border-light)',
                transition: '0.3s ease'
              }}
            >
              Income
            </button>
          </div>
        </div>

        <div>
          <label htmlFor="text" style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Description</label>
          <input
            type="text"
            id="text"
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="e.g. Groceries"
            className="glass-input"
            required
          />
        </div>

        <div>
           <label htmlFor="amount" style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Amount ($)</label>
          <input
            type="number"
            id="amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="e.g. 50.00"
            step="0.01"
            min="0"
            className="glass-input"
            required
          />
        </div>

        <div>
          <label htmlFor="category" style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Category</label>
          <select 
            id="category"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="glass-input"
            style={{ appearance: 'none', background: 'rgba(15, 23, 42, 0.8)' }}
          >
            {categories.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>

        <button className="glass-button" style={{ marginTop: '1rem' }} type="submit">
          Add Transaction
        </button>
      </form>
    </div>
  );
}
