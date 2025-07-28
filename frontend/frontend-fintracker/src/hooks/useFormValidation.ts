import { useState } from 'react';

export function useFormValidation<T>(
  initialState: T,
  validate: (values: T) => Partial<T>
) {
  const [formData, setFormData] = useState<T>(initialState);
  const [errors, setErrors] = useState<Partial<T>>({});
  const [generalError, setGeneralError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = async (
    e: React.FormEvent,
    onSubmit: (data: T) => Promise<void> | void
  ) => {
    e.preventDefault();
    const validationErrors = validate(formData);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    await onSubmit(formData);
  };

  return {
    formData,
    errors,
    handleChange,
    handleSubmit,
    setGeneralError,
    generalError
  };
}
