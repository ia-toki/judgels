import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ArchiveEditDialog } from './ArchiveEditDialog';

const archive = {
  id: 1,
  jid: 'archiveJid',
  slug: 'archive',
  name: 'Archive',
  category: 'Category',
  description: 'This is a archive',
};

describe('ArchiveEditDialog', () => {
  let onUpdateArchive;
  let wrapper;

  beforeEach(() => {
    onUpdateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      archive,
      onCloseDialog: jest.fn(),
      onUpdateArchive,
    };
    wrapper = mount(
      <Provider store={store}>
        <ArchiveEditDialog {...props} />
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    expect(slug.prop('value')).toEqual('archive');
    slug.prop('onChange')({ target: { value: 'new-archive' } });

    const name = wrapper.find('input[name="name"]');
    expect(name.prop('value')).toEqual('Archive');
    name.prop('onChange')({ target: { value: 'New archive' } });

    const category = wrapper.find('input[name="category"]');
    expect(category.prop('value')).toEqual('Category');
    category.prop('onChange')({ target: { value: 'New category' } });

    const description = wrapper.find('textarea[name="description"]');
    expect(description.prop('value')).toEqual('This is a archive');
    description.prop('onChange')({ target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateArchive).toHaveBeenCalledWith(archive.jid, {
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
