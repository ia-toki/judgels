import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { CourseEditDialog } from './CourseEditDialog';

const course = {
  id: 1,
  jid: 'courseJid',
  slug: 'course',
  name: 'Course',
  description: 'This is a course',
};

describe('CourseEditDialog', () => {
  let onUpdateCourse;

  beforeEach(() => {
    onUpdateCourse = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      course,
      onCloseDialog: vi.fn(),
      onUpdateCourse,
    };
    render(
      <Provider store={store}>
        <CourseEditDialog {...props} />
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('course');
    await user.clear(slug);
    await user.type(slug, 'new-course');

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Course');
    await user.clear(name);
    await user.type(name, 'New course');

    const description = screen.getByRole('textbox', { name: /description/i });
    expect(description).toHaveValue('This is a course');
    await user.clear(description);
    await user.type(description, 'New description');

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    expect(onUpdateCourse).toHaveBeenCalledWith(course.jid, {
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
