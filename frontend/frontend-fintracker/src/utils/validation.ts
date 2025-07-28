// src/utils/validation.ts
import { LoginDto, UserDTO } from '../interfaces/auth';

// Regex constants
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,100}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const MOBILE_REGEX = /^\d{10,15}$/;

export const validateLogin = (data: LoginDto) => {
  const errors: Partial<LoginDto> = {};
  if (!data.userName) errors.userName = 'Username cannot be empty.';
  else if (data.userName.length < 4 || data.userName.length > 30)
    errors.userName = 'Username must be between 4 and 30 characters.';

  if (!data.password) errors.password = 'Password cannot be empty.';
  else if (!PASSWORD_REGEX.test(data.password))
    errors.password =
      'Password must be 8-100 characters, include upper and lower case letters, a number, and a special character.';

  return errors;
};

interface RegisterDTO extends UserDTO {
  confirmPassword?: string;
}

export const validateRegister = (data: RegisterDTO) => {
  const errors: Partial<RegisterDTO> = {};

  if (!data.firstName) {
    errors.firstName = 'First name is required.';
  } else if (data.firstName.length < 2 || data.firstName.length > 50) {
    errors.firstName = 'First name must be between 2 and 50 characters.';
  }

  if (!data.lastName) {
    errors.lastName = 'Last name is required.';
  } else if (data.lastName.length < 2 || data.lastName.length > 50) {
    errors.lastName = 'Last name must be between 2 and 50 characters.';
  }

  if (!data.emailId) {
    errors.emailId = 'Email is required.';
  } else if (!EMAIL_REGEX.test(data.emailId)) {
    errors.emailId = 'Email should be valid.';
  }

  if (!data.mobileNumber) {
    errors.mobileNumber = 'Mobile number is required.';
  } else if (!MOBILE_REGEX.test(data.mobileNumber)) {
    errors.mobileNumber = 'Mobile number must be between 10 and 15 digits.';
  }

  if (!data.userName) {
    errors.userName = 'Username is required.';
  } else if (data.userName.length < 4 || data.userName.length > 30) {
    errors.userName = 'Username must be between 4 and 30 characters.';
  }

  if (!data.password) {
    errors.password = 'Password is required.';
  } else if (!PASSWORD_REGEX.test(data.password)) {
    errors.password =
      'Password must be 8-100 characters, include upper and lower case letters, a number, and a special character.';
  }

  if (!data.confirmPassword) {
    errors.confirmPassword = 'Please confirm your password.';
  } else if (data.password !== data.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match.';
  }

  return errors;
};
