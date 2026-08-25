import { useCountUp } from '../hooks/useCountUp';

interface ScoreCounterProps {
  value: number;
  duration?: number;
  suffix?: string;
  className?: string;
}

export default function ScoreCounter({ value, duration = 1500, suffix = '', className = '' }: ScoreCounterProps) {
  const count = useCountUp(value, duration);

  return (
    <span className={`tabular-nums font-bold ${className}`}>
      {count.toLocaleString()}{suffix}
    </span>
  );
}