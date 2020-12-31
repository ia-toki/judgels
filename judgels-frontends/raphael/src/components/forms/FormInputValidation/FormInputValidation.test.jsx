import { shallow } from 'enzyme';

import { FormInputValidation } from './FormInputValidation';

describe('FormInputValidation', () => {
  let wrapper;
  let touched;
  let valid;
  let error;

  const render = () => {
    const props = {
      meta: { touched, valid, error },
    };

    wrapper = shallow(<FormInputValidation {...props} />);
  };

  beforeEach(() => {
    touched = false;
    valid = false;
    error = 'Required';
  });

  describe('when the input is first rendered', () => {
    beforeEach(() => {
      render();
    });

    it('does not show any errors', () => {
      expect(wrapper.find('.form-text-input-error')).toHaveLength(0);
    });
  });

  describe('when the input is valid', () => {
    beforeEach(() => {
      touched = true;
      valid = true;
      render();
    });

    it('does not show any errors', () => {
      expect(wrapper.find('.form-text-input-error')).toHaveLength(0);
    });
  });

  describe('when the input is invalid', () => {
    beforeEach(() => {
      touched = true;
      valid = false;
      render();
    });

    it('shows the error', () => {
      expect(wrapper.find('.form-text-input-error').text()).toEqual('Required');
    });
  });
});
