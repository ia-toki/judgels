export class BadRequestError {
  name = 'BadRequestError';
  message: string;
  stack?: string;

  constructor(message?: string) {
    const error = new Error(message);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class UnauthorizedError {
  name = 'UnauthorizedError';
  message: string;
  stack?: string;

  constructor(message?: string) {
    const error = new Error(message);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class ForbiddenError {
  name = 'ForbiddenError';
  message: string;
  stack?: string;

  constructor(message?: string) {
    const error = new Error(message);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class NotFoundError {
  name = 'NotFoundError';
  message: string;
  stack?: string;

  constructor(message?: string) {
    const error = new Error(message);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class RemoteError {
  name = 'RemoteError';
  message: string;
  stack?: string;

  constructor(message?: string) {
    const error = new Error(message);
    this.stack = error.stack;
    this.message = error.message;
  }
}
