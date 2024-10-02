import React, { useState } from 'react';

const WorkoutLogger = ({ addWorkout }) => {
  const [exercise, setExercise] = useState('');
  const [weight, setWeight] = useState('');
  const [sets, setSets] = useState('');
  const [reps, setReps] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    addWorkout({ exercise, weight, sets, reps, date: new Date() });
    setExercise('');
    setWeight('');
    setSets('');
    setReps('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Exercise"
        value={exercise}
        onChange={(e) => setExercise(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="Weight"
        value={weight}
        onChange={(e) => setWeight(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="Sets"
        value={sets}
        onChange={(e) => setSets(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="Reps"
        value={reps}
        onChange={(e) => setReps(e.target.value)}
        required
      />
      <button type="submit">Log Workout</button>
    </form>
  );
};

export default WorkoutLogger;