import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
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
    act(() => {
      const button = wrapper.find('button');
      button.simulate('click');
    });

    wrapper.update();

    act(() => {
      const slug = wrapper.find('input[name="slug"]');
      slug.prop('onChange')({ target: { value: 'new-problemSet' } });

      const name = wrapper.find('input[name="name"]');
      name.prop('onChange')({ target: { value: 'New problemSet' } });

      const archiveSlug = wrapper.find('input[name="archiveSlug"]');
      archiveSlug.prop('onChange')({ target: { value: 'New archive' } });

      const description = wrapper.find('textarea[name="description"]');
      description.prop('onChange')({ target: { value: 'New description' } });

      const contestTime = wrapper.find('input[name="contestTime"]');
      contestTime.prop('onChange')({ target: { value: '2100-01-01 00:00' } });

      const form = wrapper.find('form');
      form.simulate('submit');
    });

    expect(onCreateProblemSet).toHaveBeenCalledWith({
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
      contestTime: parseDateTime('2100-01-01 00:00').getTime(),
    });
  });
});
