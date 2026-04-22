import { useMemo } from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip as RechartsTooltip } from 'recharts';

export default function Dashboard({ balance, income, expense, transactions }) {
  // Aggregate expenses for the chart
  const pieData = useMemo(() => {
    const expenses = transactions.filter(t => t.type === 'expense');
    const categories = expenses.reduce((acc, curr) => {
      acc[curr.category] = (acc[curr.category] || 0) + curr.amount;
      return acc;
    }, {});
    
    return Object.keys(categories).map(key => ({
      name: key,
      value: categories[key]
    })).sort((a, b) => b.value - a.value);
  }, [transactions]);

  const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#ec4899'];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      
      {/* Top Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center', borderTop: '4px solid #3b82f6' }}>
          <h3 style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1px' }}>Total Balance</h3>
          <h2 style={{ fontSize: '2.5rem', marginTop: '0.5rem' }}>${balance.toFixed(2)}</h2>
        </div>
        
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center', borderTop: '4px solid var(--positive)' }}>
          <h3 style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1px' }}>Income</h3>
          <h2 style={{ fontSize: '2rem', marginTop: '0.5rem', color: 'var(--positive)' }}>+${income.toFixed(2)}</h2>
        </div>
        
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center', borderTop: '4px solid var(--negative)' }}>
          <h3 style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1px' }}>Expense</h3>
          <h2 style={{ fontSize: '2rem', marginTop: '0.5rem', color: 'var(--negative)' }}>-${expense.toFixed(2)}</h2>
        </div>
      </div>

      {/* Chart Section */}
      <div className="glass-panel" style={{ padding: '2rem' }}>
        <h3 style={{ marginBottom: '1.5rem' }}>Expense Breakdown</h3>
        {pieData.length > 0 ? (
          <div style={{ height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={80}
                  outerRadius={110}
                  paddingAngle={5}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  labelLine={false}
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} stroke="rgba(0,0,0,0)" />
                  ))}
                </Pie>
                <RechartsTooltip 
                  contentStyle={{ backgroundColor: 'rgba(15, 23, 42, 0.9)', border: '1px solid var(--border-light)', borderRadius: '8px' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div style={{ height: 200, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
            No expenses logged yet. Add some to see the chart!
          </div>
        )}
      </div>
      
    </div>
  );
}
