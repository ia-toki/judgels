import { render, screen } from '@testing-library/react';

import { FormInputValidation } from './FormInputValidation';

describe('FormInputValidation', () => {
  let touched;
  let valid;
  let error;

  const renderComponent = () => {
    const props = {
      meta: { touched, valid, error },
    };

    render(<FormInputValidation {...props} />);
  };

  beforeEach(() => {
    touched = false;
    valid = false;
    error = 'Required';
  });

  describe('when the input is first rendered', () => {
    beforeEach(() => {
      renderComponent();
    });

    it('does not show any errors', () => {
      expect(screen.queryByText('Required')).not.toBeInTheDocument();
    });
  });

  describe('when the input is valid', () => {
    beforeEach(() => {
      touched = true;
      valid = true;
      renderComponent();
    });

    it('does not show any errors', () => {
      expect(screen.queryByText('Required')).not.toBeInTheDocument();
    });
  });

  describe('when the input is invalid', () => {
    beforeEach(() => {
      touched = true;
      valid = false;
      renderComponent();
    });

    it('shows the error', () => {
      expect(screen.getByText('Required')).toBeInTheDocument();
    });
  });
});
