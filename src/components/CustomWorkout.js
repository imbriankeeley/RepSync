import React, { useState } from 'react';

const CustomWorkout = ({ addCustomWorkout }) => {
  const [name, setName] = useState('');
  const [exercises, setExercises] = useState([]);

  const addExercise = () => {
    setExercises([...exercises, { name: '', sets: '', reps: '' }]);
  };

  const handleExerciseChange = (index, field, value) => {
    const newExercises = exercises.slice();
    newExercises[index][field] = value;
    setExercises(newExercises);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    addCustomWorkout({ name, exercises });
    setName('');
    setExercises([]);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Workout Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />
      {exercises.map((exercise, index) => (
        <div key={index}>
          <input
            type="text"
            placeholder="Exercise Name"
            value={exercise.name}
            onChange={(e) => handleExerciseChange(index, 'name', e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Sets"
            value={exercise.sets}
            onChange={(e) => handleExerciseChange(index, 'sets', e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Reps"
            value={exercise.reps}
            onChange={(e) => handleExerciseChange(index, 'reps', e.target.value)}
            required
          />
        </div>
      ))}
      <button type="button" onClick={addExercise}>Add Exercise</button>
      <button type="submit">Save Workout</button>
    </form>
  );
};

export default CustomWorkout;