export interface SourceFile {
  name: string;
  content: string;
}

export interface SubmissionSource {
  files: { [key: string]: SourceFile };
}
