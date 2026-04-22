export default function TransactionList({ transactions, onDelete }) {
  if (transactions.length === 0) {
    return (
      <div className="glass-panel" style={{ padding: '2rem', marginTop: '2rem', textAlign: 'center' }}>
        <h3 style={{ color: 'var(--text-muted)' }}>No recent transactions</h3>
      </div>
    );
  }

  return (
    <div className="glass-panel" style={{ padding: '2rem', marginTop: '2rem' }}>
      <h3 style={{ marginBottom: '1.5rem' }}>Recent Transactions</h3>
      
      <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {transactions.map(t => (
          <li 
            key={t.id} 
            style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center',
              padding: '1rem 1.5rem',
              background: 'rgba(30, 41, 59, 0.4)',
              borderRadius: 'var(--radius-md)',
              borderLeft: `4px solid ${t.type === 'income' ? 'var(--positive)' : 'var(--negative)'}`,
              transition: 'var(--transition-fast)'
            }}
            onMouseOver={(e) => e.currentTarget.style.transform = 'translateX(5px)'}
            onMouseOut={(e) => e.currentTarget.style.transform = 'translateX(0)'}
          >
            <div>
              <p style={{ fontWeight: 600, fontSize: '1.05rem', marginBottom: '0.2rem' }}>{t.text}</p>
              <div style={{ display: 'flex', gap: '1rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                <span style={{ 
                  background: 'rgba(255,255,255,0.1)', 
                  padding: '2px 8px', 
                  borderRadius: '12px' 
                }}>
                  {t.category}
                </span>
                <span>{new Date(t.date).toLocaleDateString()}</span>
              </div>
            </div>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
              <span style={{ 
                fontWeight: 700, 
                fontSize: '1.2rem',
                color: t.type === 'income' ? 'var(--positive)' : 'var(--text-main)'
              }}>
                {t.type === 'income' ? '+' : '-'}${t.amount.toFixed(2)}
              </span>
              
              <button 
                onClick={() => onDelete(t.id)}
                className="glass-button danger"
                style={{ padding: '8px 12px', fontSize: '0.9rem' }}
                title="Delete"
              >
                ✕
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
