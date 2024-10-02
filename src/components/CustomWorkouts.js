import React from 'react';

const WorkoutCalendar = ({ customWorkouts }) => {
  return (
    <div>
      <h2>Custom Workouts</h2>
      <ul>
        {customWorkouts.map(workout => (
            <li key={workout.name}>
                {workout.name}
                <ul>
                {workout.exercises.map((exercise, index) => (
                    <li key={index}>
                    {exercise.name} - {exercise.sets} sets, {exercise.reps} reps
                    </li>
                ))}
                </ul>
            </li>
        ))}
      </ul>
    </div>
  );
};

export default WorkoutCalendar;