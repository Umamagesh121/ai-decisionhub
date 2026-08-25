import { motion } from 'framer-motion';
import { Check } from 'lucide-react';

interface StepIndicatorProps {
  steps: string[];
  currentStep: number;
  onStepClick?: (step: number) => void;
}

export default function StepIndicator({ steps, currentStep, onStepClick }: StepIndicatorProps) {
  return (
    <div className="w-full overflow-x-auto pb-2">
      <div className="flex items-center min-w-fit px-1">
        {steps.map((label, i) => (
          <div key={i} className="flex items-center flex-1 min-w-0 last:flex-none">
            <button
              onClick={() => onStepClick?.(i)}
              disabled={i > currentStep}
              className="flex flex-col items-center gap-1.5 min-w-0"
            >
              <motion.div
                animate={{
                  scale: i === currentStep ? 1.1 : 1,
                  backgroundColor:
                    i < currentStep
                      ? 'rgb(16, 185, 129)'
                      : i === currentStep
                      ? 'rgb(99, 102, 241)'
                      : 'rgb(209, 213, 219)',
                }}
                className={`w-8 h-8 rounded-full flex items-center justify-center text-white transition-colors flex-shrink-0 ${
                  i > currentStep ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'
                }`}
              >
                {i < currentStep ? (
                  <Check className="w-4 h-4" />
                ) : (
                  <span className="text-xs font-bold">{i + 1}</span>
                )}
              </motion.div>
              <span
                className={`text-[10px] sm:text-xs font-medium text-center truncate max-w-[60px] sm:max-w-[80px] ${
                  i <= currentStep
                    ? 'text-gray-900 dark:text-gray-100'
                    : 'text-gray-400 dark:text-gray-600'
                }`}
              >
                {label}
              </span>
            </button>
            {i < steps.length - 1 && (
              <div className="flex-1 h-0.5 mx-1 sm:mx-2 min-w-[20px]">
                <motion.div
                  className="h-full rounded-full"
                  initial={{ width: '0%' }}
                  animate={{
                    width: i < currentStep ? '100%' : '0%',
                    backgroundColor: i < currentStep ? 'rgb(16, 185, 129)' : 'rgb(209, 213, 219)',
                  }}
                  transition={{ duration: 0.5 }}
                />
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}