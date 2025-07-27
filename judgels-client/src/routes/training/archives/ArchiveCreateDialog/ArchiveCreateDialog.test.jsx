import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ArchiveCreateDialog } from './ArchiveCreateDialog';

describe('ArchiveCreateDialog', () => {
  let onGetArchiveConfig;
  let onCreateArchive;
  let wrapper;

  beforeEach(() => {
    onCreateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetArchiveConfig,
      onCreateArchive,
    };
    wrapper = mount(
      <Provider store={store}>
        <ArchiveCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const slug = wrapper.find('input[name="slug"]');
    slug.prop('onChange')({ target: { value: 'new-archive' } });

    const name = wrapper.find('input[name="name"]');
    name.prop('onChange')({ target: { value: 'New archive' } });

    const category = wrapper.find('input[name="category"]');
    category.prop('onChange')({ target: { value: 'New category' } });

    const description = wrapper.find('textarea[name="description"]');
    description.prop('onChange')({ target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateArchive).toHaveBeenCalledWith({
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
