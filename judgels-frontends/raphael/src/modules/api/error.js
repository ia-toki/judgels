export class BadRequestError {
  name = 'BadRequestError';
  message;
  stack;

  constructor(message) {
    const error = new Error(message && message.errorName);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class UnauthorizedError {
  name = 'UnauthorizedError';
  message;
  stack;

  constructor(message) {
    const error = new Error(message && message.errorName);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class ForbiddenError {
  name = 'ForbiddenError';
  message;
  parameters;
  stack;

  constructor(message) {
    const error = new Error(message && message.errorName);
    this.stack = error.stack;
    this.parameters = (message && message.parameters) || {};
    this.message = error.message;
  }
}

export class NotFoundError {
  name = 'NotFoundError';
  message;
  stack;

  constructor(message) {
    const error = new Error(message && message.errorName);
    this.stack = error.stack;
    this.message = error.message;
  }
}

export class RemoteError {
  name = 'RemoteError';
  message;
  stack;

  constructor(message) {
    const error = new Error(message && message.errorName);
    this.stack = error.stack;
    this.message = error.message;
  }
}
