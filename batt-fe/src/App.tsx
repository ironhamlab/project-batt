import { Outlet, createBrowserRouter, RouterProvider } from "react-router-dom";
import MainPage from "./pages/MainPage";
import TransferPage from "./pages/TransferPage";
import TransferPerformancePage from "./pages/TransferPerformancePage";
import BookingPage from "./pages/BookingPage";
import { CssBaseline } from "@mui/joy";
import PaymentPage from "./pages/PaymentPage";
import { PaymentSuccessPage } from "./pages/PaymentSuccessPage";
import { FailPage } from "./pages/FailPage";
import Header from "./components/Header";
import PerformanceInfoPage from "./pages/PerformanceInfoPage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import MypageLayout from "./pages/MypageLayout";
import "./app/globals.css";
import SearchResultPage from "./pages/SearchResultPage";
import LoginRedirectPage from "./pages/LoginRedirectPage";
import ProtectRoute from "./lib/auth/ProtectRoute";
import ReverseProtectRoute from "./lib/auth/ReverseProtectRouter";
import NotFoundPage from "./pages/NotFoundPage";
import Footer from "./components/Footer";

const LayoutWithHeader = () => {
  return (
    <>
      <div
        style={{ height: "100vh", display: "flex", flexDirection: "column" }}
      >
        <Header />
        <div style={{ flex: 1 }}>
          <Outlet />
        </div>
        <Footer />
      </div>
    </>
  );
};
const router = createBrowserRouter([
  {
    element: <LayoutWithHeader />,
    children: [
      { path: "/", element: <MainPage /> },
      { path: "search", element: <SearchResultPage /> },
      { path: "transfer", element: <TransferPerformancePage /> },
      { path: "transfer-performance/:performanceId", element: <TransferPage /> },
      { path: "performance/:performanceId", element: <PerformanceInfoPage /> },
    ],
  },
  {
    path: "/booking",
    element: (
      <ProtectRoute>
        <BookingPage />
      </ProtectRoute>
    ),
  },
  {
    path: "/payment",
    element: (
      <ProtectRoute>
        <PaymentPage />
      </ProtectRoute>
    ),
  },
  {
    path: "/success",
    element: (
      <ProtectRoute>
        <PaymentSuccessPage />
      </ProtectRoute>
    ),
  },
  {
    path: "/fail",
    element: (
      <ProtectRoute>
        <FailPage />
      </ProtectRoute>
    ),
  },
  {
    path: "/mypage/*",
    element: (
      <ProtectRoute>
        <MypageLayout />
      </ProtectRoute>
    ),
  },
  {
    path: "/login",
    element: (
      <ReverseProtectRoute>
        <LoginPage />
      </ReverseProtectRoute>
    ),
  },
  {
    path: "/logout",
    element: (
      <ProtectRoute>
        <LogoutPage />
      </ProtectRoute>
    ),
  },
  { path: "/login/callback", element: <LoginRedirectPage /> },
  { path: "*", element: <NotFoundPage /> },
]);

function App() {
  return (
    <>
      <CssBaseline />
      <RouterProvider router={router} />
      {/* <Routes>
        <Route element={<LayoutWithHeader />}>
          <Route path="/" element={<MainPage />} />
          <Route path="/search" element={<SearchResultPage />} />
          <Route path="/transfer" element={<TransferPerformancePage />} />
          <Route
            path="/transfer-performance/:performanceId"
            element={<TransferPage />}
          />
          <Route
            path="/performance/:performanceId"
            element={<PerformanceInfoPage />}
          />
        </Route>
        <Route
          path="/booking"
          element={
            <ProtectRoute>
              <BookingPage />
            </ProtectRoute>
          }
        />
        <Route
          path="/payment"
          element={
            <ProtectRoute>
              <PaymentPage />
            </ProtectRoute>
          }
        />
        <Route
          path="/success"
          element={
            <ProtectRoute>
              <PaymentSuccessPage />
            </ProtectRoute>
          }
        />
        <Route
          path="/fail"
          element={
            <ProtectRoute>
              <FailPage />
            </ProtectRoute>
          }
        />
        <Route
          path="/mypage/*"
          element={
            <ProtectRoute>
              <MypageLayout />
            </ProtectRoute>
          }
        />
        <Route
          path="/login"
          element={
            <ReverseProtectRoute>
              <LoginPage />
            </ReverseProtectRoute>
          }
        />
        <Route
          path="/logout"
          element={
            <ProtectRoute>
              <LogoutPage />
            </ProtectRoute>
          }
        />
        <Route path="/login/callback" element={<LoginRedirectPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes> */}
    </>
  );
}

export default App;
