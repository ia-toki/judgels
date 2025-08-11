import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import createMockStore from 'redux-mock-store';

import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementCreateDialog } from './ContestAnnouncementCreateDialog';

describe('ContestAnnouncementCreateDialog', () => {
  let onCreateAnnouncement;
  let wrapper;

  beforeEach(() => {
    onCreateAnnouncement = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onCreateAnnouncement,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestAnnouncementCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    act(() => {
      const button = wrapper.find('button');
      button.simulate('click');
    });

    wrapper.update();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = wrapper.find('button[data-key="status"]');
    // status.simulate('click');

    act(() => {
      const title = wrapper.find('input[name="title"]');
      title.prop('onChange')({
        target: { value: 'Snack' },
      });

      const content = wrapper.find('textarea[name="content"]');
      content.prop('onChange')({
        target: { value: 'Snack is provided.' },
      });

      const form = wrapper.find('form');
      form.simulate('submit');
    });

    expect(onCreateAnnouncement).toHaveBeenCalledWith('contestJid', {
      title: 'Snack',
      content: 'Snack is provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});
