import { Refine } from "@refinedev/core";
import { DevtoolsPanel, DevtoolsProvider } from "@refinedev/devtools";
import { RefineKbar, RefineKbarProvider } from "@refinedev/kbar";

import {
  ErrorComponent,
  notificationProvider,
  RefineSnackbarProvider,
  ThemedLayoutV2,
  ThemedTitleV2,
} from "@refinedev/mui";

import CssBaseline from "@mui/material/CssBaseline";
import GlobalStyles from "@mui/material/GlobalStyles";
import routerBindings, {
  DocumentTitleHandler,
  NavigateToResource,
  UnsavedChangesNotifier,
} from "@refinedev/react-router-v6";
import dataProvider from "@refinedev/simple-rest";
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import { Header } from "./components/header";
import { ColorModeContextProvider } from "./contexts/color-mode";
import {
  BlogPostCreate,
  BlogPostEdit,
  BlogPostList,
  BlogPostShow,
} from "./pages/blog-posts";
import {
  JourneyList,
  JourneyShow,
  JourneyEdit,
} from "./pages/journeys";
import {
  MessageList,
  MessageShow,
} from "./pages/messages";
import {
  EnvList,
  EnvShow,
} from "./pages/environments";

function App() {
  return (
    <BrowserRouter>
      <RefineKbarProvider>
        <ColorModeContextProvider>
          <CssBaseline />
          <GlobalStyles styles={{ html: { WebkitFontSmoothing: "auto" } }} />
          <RefineSnackbarProvider>
            <DevtoolsProvider>
              <Refine
                dataProvider={dataProvider("https://api.fake-rest.refine.dev")}
                notificationProvider={notificationProvider}
                routerProvider={routerBindings}
                resources={[
                  {
                    name: "blog_posts",
                    list: "/blog-posts",
                    create: "/blog-posts/create",
                    edit: "/blog-posts/edit/:id",
                    show: "/blog-posts/show/:id",
                    meta: {
                      canDelete: true,
                    },
                  },
                  {
                    name: "journeys",
                    list: "/journeys",
                    show: "/journeys/show/:id",
                    edit: "/journeys/edit/:id",
                    // create: "/categories/create",
                    // meta: {
                    //   canDelete: true,
                    // },
                  },
                  {
                    name: "messages",
                    list: "/messages",
                    show: "/messages/show/:id",
                  },
                  {
                    name: "environments",
                    list: "/environments",
                    show: "/environments/show/:id",
                  },
                ]}
                options={{
                  syncWithLocation: true,
                  warnWhenUnsavedChanges: true,
                  useNewQueryKeys: true,
                  projectId: "NLDmxk-VEd6I9-9rGwYp",
                }}
              >
                <Routes>
                  <Route
                    element={
                      <ThemedLayoutV2
                        Title={({ collapsed }) => (
                          <ThemedTitleV2
                            collapsed={collapsed}
                            text="Kinect Messaging"
                            icon={
                              <svg className="w-10 h-10 fill-current" viewBox="0 0 100 95" xmlns="http://www.w3.org/2000/svg">
                                <g d-rs="1" transform="matrix(0,1,-1,0,96.8564,-2.9118)">
                                  <path d="M68.7 7c1.44-.16 2.17.38 2.1 1.64-.28 5.07 2.81 7.84 6.71 10.18 4.19 2.52 8.73 4.47 12.45 7.75 4 3.47 5.88 7.93 6.61 13 .07.48.22 1.09-.41 1.28s-1-.41-1.42-.8a61.32 61.32 0 0 0-9.35-8.19C72.05 23.07 57.64 20 42 23.31c-.62.13-1.46.63-1.84-.06s.52-1.29 1-1.79a51.63 51.63 0 0 1 27-14.41c.25-.05.49-.05.54-.05zM33.33 66.38a39.52 39.52 0 0 1-16.14-5.92 60.65 60.65 0 0 1-11.69-10C4.14 49 3.75 48.11 6 47c3.47-1.8 4.46-5.17 4.61-8.81.22-5-.29-9.94.47-14.9.84-5.5 3.17-10.08 7.71-13.42.52-.37 1-1.1 1.72-.66s.26 1.2.09 1.81A66.45 66.45 0 0 0 18 24.39c-.61 8.85 1.42 17.11 4.72 25.17a58.87 58.87 0 0 0 10.61 16.82zM76.83 66.43c.54 5.69-1.22 11.09-2.72 16.52-.46 1.68-1.11 2.62-3.11 1.28-3.65-2.43-7.34-1.26-10.81.63-4 2.17-7.78 4.76-11.94 6.65a18.77 18.77 0 0 1-15.67.16c-.62-.26-1.67-.36-1.5-1.35.14-.74 1.06-.7 1.68-.84a53.77 53.77 0 0 0 20-9A54.11 54.11 0 0 0 73 52.57c.19-.61.15-1.58.89-1.63 1-.06.94 1 1.12 1.66a40.25 40.25 0 0 1 1.82 13.83z" fill="#2F67FF" />
                                  <path d="M29.41 86.58c-1.5.09-1.94-.75-1.85-1.88.36-4.46-2.23-7.12-5.63-9.23-2.53-1.57-5.16-3-7.76-4.47-6.25-3.56-11-8.24-12.12-15.76-.1-.71-.62-1.63.12-2.09.93-.58 1.5.42 2 1a51.77 51.77 0 0 0 21.62 14.2c9.91 3.37 20 4.67 30.35 2.17.55-.13 1.22-.47 1.64.07s-.15 1.06-.53 1.45c-7.59 7.83-16.9 12.41-27.51 14.52a1.58 1.58 0 0 1-.33.02zM24.31 43.12c-1.95-3.73-2.33-7.23-2.64-10.66a48.29 48.29 0 0 1 2.67-21.82c.66-1.81 1.34-2.34 3.21-1.17 4.11 2.6 8 1 11.69-1.2a102 102 0 0 1 11.55-6.35c5.64-2.45 11.11-2 16.75 1.25-21.65 5.3-35.86 18.6-43.23 39.95zM65.52 27.58c.68-.58 1.42-.27 2.12-.09C77.7 30 86 35.5 92.84 43.17c1.17 1.29 1.64 2.32-.43 3.42-4 2.15-4.84 6.13-5 10.22-.16 3.9 0 7.81-.06 11.71-.13 6.08-2.43 11.13-7.35 14.83-.61.46-1.2 1.52-2.07.87-.68-.5 0-1.4.17-2.08a55 55 0 0 0-2.34-38.25A47.84 47.84 0 0 0 66.34 29c-.34-.36-.96-.69-.82-1.42z" fill="#36B82A" />
                                  <path d="M77.46 14.66a4.5 4.5 0 0 1-4.56-4.57 4.8 4.8 0 0 1 4.68-4.54 4.55 4.55 0 0 1 4.34 4.79 4.24 4.24 0 0 1-4.46 4.32z" fill="#2F67FF" />
                                  <path d="M4.37 36.72a4.51 4.51 0 1 1 0 9A4.32 4.32 0 0 1 0 41.31a4.2 4.2 0 0 1 4.37-4.59zM72.36 89a4.52 4.52 0 1 1-9 0 4.21 4.21 0 0 1 4.34-4.4 4.39 4.39 0 0 1 4.66 4.4z" fill="#2F67FF" />
                                  <path d="M32.12 8.91a4.17 4.17 0 0 1-4.59-4.12A4.62 4.62 0 0 1 31.89 0a4.57 4.57 0 0 1 4.6 4.54 4.17 4.17 0 0 1-4.37 4.37zM95.38 56.78a4.59 4.59 0 0 1-4.52-4.35 4.68 4.68 0 0 1 4.65-4.54 4.5 4.5 0 0 1 4.27 4.61 4.06 4.06 0 0 1-4.4 4.28zM22.21 79.1a4.65 4.65 0 0 1 4.7 4.16 5.18 5.18 0 0 1-4.53 4.79A4.62 4.62 0 0 1 18 83.56a4.1 4.1 0 0 1 4.21-4.46z" fill="#36B82A" />
                                </g>
                              </svg>
                            }
                          />
                        )}
                        Header={() => <Header sticky />}>
                        <Outlet />
                      </ThemedLayoutV2>
                    }
                  >
                    <Route
                      index
                      element={<NavigateToResource resource="journeys" />}
                    />
                    <Route path="/blog-posts">
                      <Route index element={<BlogPostList />} />
                      {/* <Route path="create" element={<BlogPostCreate />} /> */}
                      <Route path="edit/:id" element={<BlogPostEdit />} />
                      <Route path="show/:id" element={<BlogPostShow />} />
                    </Route>
                    {/* <Route path="/categories">
                      <Route index element={<CategoryList />} />
                      <Route path="create" element={<CategoryCreate />} />
                      <Route path="edit/:id" element={<CategoryEdit />} />
                      <Route path="show/:id" element={<CategoryShow />} />
                    </Route> */}
                    <Route path="/journeys">
                      <Route index element={<JourneyList />} />
                      <Route path="show/:id" element={<JourneyShow />} />
                      <Route path="edit/:id" element={<JourneyEdit />} />
                      {/* <Route path="create" element={<CategoryCreate />} />
                      <Route path="show/:id" element={<CategoryShow />} /> */}
                    </Route>
                    <Route path="/messages">
                      <Route index element={<MessageList />} />
                      <Route path="show/:id" element={<MessageShow />} />
                    </Route>
                    <Route path="/environments">
                      <Route index element={<EnvList />} />
                      <Route path="show/:id" element={<EnvShow />} />
                    </Route>
                    <Route path="*" element={<ErrorComponent />} />
                  </Route>
                </Routes>

                <RefineKbar />
                <UnsavedChangesNotifier />
                <DocumentTitleHandler />
              </Refine>
            </DevtoolsProvider>
          </RefineSnackbarProvider>
        </ColorModeContextProvider>
      </RefineKbarProvider>
    </BrowserRouter>
  );
}

export default App;
