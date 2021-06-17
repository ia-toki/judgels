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
    slug.getDOMNode().value = 'new-archive';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'New archive';
    name.simulate('input');

    const category = wrapper.find('input[name="category"]');
    category.getDOMNode().value = 'New category';
    category.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

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
