import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ContestCreateDialog } from './ContestCreateDialog';

describe('ContestCreateDialog', () => {
  let onCreateContest;
  let wrapper;

  beforeEach(() => {
    onCreateContest = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createMockStore()({});

    const props = {
      onCreateContest,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const slug = wrapper.find('input[name="slug"]');
    slug.prop('onChange')({ target: { value: 'new-contest' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateContest).toHaveBeenCalledWith({ slug: 'new-contest' });
  });
});
