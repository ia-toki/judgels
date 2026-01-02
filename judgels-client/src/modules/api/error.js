export class BadRequestError extends Error {
  constructor(response) {
    super(response?.message);
    this.name = 'BadRequestError';
  }
}

export class UnauthorizedError extends Error {
  constructor(response) {
    super(response?.message);
    this.name = 'UnauthorizedError';
  }
}

export class ForbiddenError extends Error {
  constructor(response) {
    super(response?.message);
    this.name = 'ForbiddenError';
    this.args = response?.args || {};
  }
}

export class NotFoundError extends Error {
  constructor(response) {
    super(response?.message);
    this.name = 'NotFoundError';
  }
}

export class RemoteError extends Error {
  constructor(response) {
    super(response?.message);
    this.name = 'RemoteError';
  }
}
