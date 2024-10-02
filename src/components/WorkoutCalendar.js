import React from 'react';

const WorkoutCalendar = ({ workouts }) => {
  return (
    <div>
      <h2>Completed Workouts</h2>
      <ul>
        {workouts.map((workout, index) => (
          <li key={index}>
            {workout.date.toLocaleDateString()}: {workout.exercise} - {workout.weight} lbs, {workout.sets} sets, {workout.reps} reps
          </li>
        ))}
      </ul>
    </div>
  );
};

export default WorkoutCalendar;