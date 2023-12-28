import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import configureMockStore from 'redux-mock-store';

import { parseDateTime } from '../../../../utils/datetime';

import { ProblemSetCreateDialog } from './ProblemSetCreateDialog';

describe('ProblemSetCreateDialog', () => {
  let onGetProblemSetConfig;
  let onCreateProblemSet;
  let wrapper;

  beforeEach(() => {
    onCreateProblemSet = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetProblemSetConfig,
      onCreateProblemSet,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSetCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const slug = wrapper.find('input[name="slug"]');
    slug.getDOMNode().value = 'new-problemSet';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'New problemSet';
    name.simulate('input');

    const archiveSlug = wrapper.find('input[name="archiveSlug"]');
    archiveSlug.getDOMNode().value = 'New archive';
    archiveSlug.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

    const contestTime = wrapper.find('input[name="contestTime"]');
    contestTime.getDOMNode().value = '2100-01-01 00:00';
    contestTime.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateProblemSet).toHaveBeenCalledWith({
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
      contestTime: parseDateTime('2100-01-01 00:00').getTime(),
    });
  });
});
