import { render, screen } from '@testing-library/react';

import { FormInputValidation } from './FormInputValidation';

describe('FormInputValidation', () => {
  const renderComponent = ({ touched = false, valid = false, error = 'Required' } = {}) => {
    render(<FormInputValidation meta={{ touched, valid, error }} />);
  };

  test('when first rendered, does not render any errors', () => {
    renderComponent();
    expect(screen.queryByText('Required')).not.toBeInTheDocument();
  });

  test('when valid, does not render any errors', () => {
    renderComponent({ touched: true, valid: true });
    expect(screen.queryByText('Required')).not.toBeInTheDocument();
  });

  test('when invalid, renders the error', () => {
    renderComponent({ touched: true, valid: false });
    expect(screen.getByText('Required')).toBeInTheDocument();
  });
});
