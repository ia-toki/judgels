export class SubmissionError {
  errors;
  constructor(errors) {
    this.errors = errors;
  }
}

export function withSubmissionError(onSubmit) {
  return async data => {
    try {
      return await onSubmit(data);
    } catch (error) {
      if (error instanceof SubmissionError) {
        return error.errors;
      }
      throw error;
    }
  };
}
