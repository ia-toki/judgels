import { render, screen } from '@testing-library/react';

import { FormInputValidation } from './FormInputValidation';

describe('FormInputValidation', () => {
  const renderComponent = ({
    touched = false,
    modified = false,
    submitFailed = false,
    valid = false,
    error = 'Required',
  } = {}) => {
    render(<FormInputValidation meta={{ touched, modified, submitFailed, valid, error }} />);
  };

  test('when first rendered, does not render any errors', () => {
    renderComponent();
    expect(screen.queryByText('Required')).not.toBeInTheDocument();
  });

  test('when valid, does not render any errors', () => {
    renderComponent({ touched: true, modified: true, valid: true });
    expect(screen.queryByText('Required')).not.toBeInTheDocument();
  });

  test('when touched but not modified, does not render any errors', () => {
    renderComponent({ touched: true, modified: false, valid: false });
    expect(screen.queryByText('Required')).not.toBeInTheDocument();
  });

  test('when invalid, renders the error', () => {
    renderComponent({ touched: true, modified: true, valid: false });
    expect(screen.getByText('Required')).toBeInTheDocument();
  });

  test('when submit failed, renders the error', () => {
    renderComponent({ submitFailed: true, valid: false });
    expect(screen.getByText('Required')).toBeInTheDocument();
  });
});
