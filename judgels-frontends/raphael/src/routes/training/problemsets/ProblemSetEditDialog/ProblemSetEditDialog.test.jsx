import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';
import { parseDateTime } from '../../../../utils/datetime';

import { ProblemSetEditDialog } from './ProblemSetEditDialog';

const problemSet = {
  id: 1,
  jid: 'problemSetJid',
  slug: 'problemSet',
  name: 'ProblemSet',
  archiveJid: 'JIDARCH',
  description: 'This is a problemSet',
  contestTime: '1970-01-01 00:00',
};

describe('ProblemSetEditDialog', () => {
  let onUpdateProblemSet;
  let wrapper;

  beforeEach(() => {
    onUpdateProblemSet = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      isOpen: true,
      problemSet,
      archiveSlug: 'archive',
      onCloseDialog: jest.fn(),
      onUpdateProblemSet,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSetEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'new-problemSet' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New problemSet' } });

    const archiveSlug = wrapper.find('input[name="archiveSlug"]');
    archiveSlug.simulate('change', { target: { value: 'New archive' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

    const contestTime = wrapper.find('input[name="contestTime"]');
    contestTime.simulate('change', { target: { value: '2100-01-01 00:00' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateProblemSet).toHaveBeenCalledWith(problemSet.jid, {
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
      contestTime: parseDateTime('2100-01-01 00:00'),
    });
  });
});
