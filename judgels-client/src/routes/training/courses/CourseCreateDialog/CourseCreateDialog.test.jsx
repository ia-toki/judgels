import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { CourseCreateDialog } from './CourseCreateDialog';

describe('CourseCreateDialog', () => {
  let onGetCourseConfig;
  let onCreateCourse;
  beforeEach(() => {
    onCreateCourse = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetCourseConfig,
      onCreateCourse,
    };
    render(
      <Provider store={store}>
        <CourseCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const slug = screen.getByRole('textbox', { name: /slug/i });
    await user.type(slug, 'new-course');

    const name = screen.getByRole('textbox', { name: /name/i });
    await user.type(name, 'New course');

    const description = screen.getByRole('textbox', { name: /description/i });
    await user.type(description, 'New description');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateCourse).toHaveBeenCalledWith({
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
