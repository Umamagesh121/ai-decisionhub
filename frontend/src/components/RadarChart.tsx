import {
  RadarChart as RechartsRadar,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar,
  ResponsiveContainer,
  Legend,
} from 'recharts';

interface RadarChartProps {
  factors: string[];
  data: { name: string; values: number[]; color: string }[];
}

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export default function RadarChart({ factors, data }: RadarChartProps) {
  const chartData = factors.map((factor, i) => {
    const point: Record<string, unknown> = { factor };
    data.forEach((d) => {
      point[d.name] = d.values[i] ?? 0;
    });
    return point;
  });

  return (
    <ResponsiveContainer width="100%" height={320}>
      <RechartsRadar data={chartData}>
        <PolarGrid stroke="#9ca3af" />
        <PolarAngleAxis
          dataKey="factor"
          tick={{ fontSize: 11, fill: '#6b7280' }}
        />
        <PolarRadiusAxis
          angle={90}
          domain={[0, 10]}
          tick={{ fontSize: 10, fill: '#9ca3af' }}
          tickCount={6}
        />
        {data.map((d, i) => (
          <Radar
            key={d.name}
            name={d.name}
            dataKey={d.name}
            stroke={d.color || COLORS[i % COLORS.length]}
            fill={d.color || COLORS[i % COLORS.length]}
            fillOpacity={0.15}
            strokeWidth={2}
            animationDuration={1200}
            animationEasing="ease-out"
          />
        ))}
        {data.length > 1 && <Legend />}
      </RechartsRadar>
    </ResponsiveContainer>
  );
}