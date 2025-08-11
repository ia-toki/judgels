import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { parseDateTime } from '../../../../utils/datetime';
import { ProblemSetEditDialog } from './ProblemSetEditDialog';

const problemSet = {
  id: 1,
  jid: 'problemSetJid',
  slug: 'problemset',
  name: 'Problem Set',
  archiveJid: 'JIDARCH',
  description: 'This is a problem set',
  contestTime: parseDateTime('1970-01-01 00:00').getTime(),
};

describe('ProblemSetEditDialog', () => {
  let onUpdateProblemSet;
  let wrapper;

  beforeEach(() => {
    onUpdateProblemSet = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      problemSet,
      archiveSlug: 'archive',
      onCloseDialog: jest.fn(),
      onUpdateProblemSet,
    };
    wrapper = mount(
      <Provider store={store}>
        <ProblemSetEditDialog {...props} />
      </Provider>
    );
  });

  test('edit dialog form', () => {
    act(() => {
      const slug = wrapper.find('input[name="slug"]');
      expect(slug.prop('value')).toEqual('problemset');
      slug.prop('onChange')({ target: { value: 'new-problemset' } });

      const name = wrapper.find('input[name="name"]');
      expect(name.prop('value')).toEqual('Problem Set');
      name.prop('onChange')({ target: { value: 'New Problem Set' } });

      const archiveSlug = wrapper.find('input[name="archiveSlug"]');
      expect(archiveSlug.prop('value')).toEqual('archive');
      archiveSlug.prop('onChange')({ target: { value: 'new-archive' } });

      const description = wrapper.find('textarea[name="description"]');
      expect(description.prop('value')).toEqual('This is a problem set');
      description.prop('onChange')({ target: { value: 'New description' } });

      const contestTime = wrapper.find('input[name="contestTime"]');
      contestTime.prop('onChange')({ target: { value: '2100-01-01 00:00' } });

      const form = wrapper.find('form');
      form.simulate('submit');
    });

    expect(onUpdateProblemSet).toHaveBeenCalledWith(problemSet.jid, {
      slug: 'new-problemset',
      name: 'New Problem Set',
      archiveSlug: 'new-archive',
      description: 'New description',
      contestTime: parseDateTime('2100-01-01 00:00').getTime(),
    });
  });
});
