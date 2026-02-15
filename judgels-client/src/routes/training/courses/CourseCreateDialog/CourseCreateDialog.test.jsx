import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { CourseCreateDialog } from './CourseCreateDialog';

describe('CourseCreateDialog', () => {
  let onGetCourseConfig;
  let onCreateCourse;
  beforeEach(() => {
    onCreateCourse = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      onGetCourseConfig,
      onCreateCourse,
    };
    render(<CourseCreateDialog {...props} />);
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
